package hdlparser;

import java.util.HashMap;
import java.util.Hashtable;

import simulationEngine.Signal;
import symbols.Symbol;
import symbols.Variable;

/**
 * Visitor class for HDL for to be called once after parse in order to ...
 *  ... create a symbol table
 *  ... check for duplicate / undefined identifiers
 *  ... check width of signal assignments
 *  
 * @author Marvin
 *
 */

public class CheckVisitor implements HDLParserVisitor {
	
	private HashMap<String, Symbol> st;
	public HashMap<String, Symbol> getSymbolTable() {
		return st;
	}
	public CheckVisitor(HashMap<String,Symbol> st) {
		this.st = st;
	}
	
	// Extends RuntimeException instead of Exception as the visitor interface is
	// created by JJtree without "throws"
	// Therefore, the caller of the visitor has to handle these kinds of
	// exceptions carefully!
	public class HDLCheckException extends RuntimeException {
		public HDLCheckException(String s, SimpleNode n) {
			super("At line " + n.jjtGetFirstToken().beginLine + ", column " + n.jjtGetFirstToken().beginColumn + ": " + s);
		}
		public HDLCheckException(Exception e, SimpleNode n) {  //Repack
			super("At line " + n.jjtGetFirstToken().beginLine + ", column " + n.jjtGetFirstToken().beginColumn + ": " + e.getMessage());
		}
		public HDLCheckException() {
		}
	}

	@Override
	public Object visit(SimpleNode node, Object data) {
		assert false;
		return null;
	}

	@Override
	public Object visit(ASTfile node, Object data) {
		int n = node.jjtGetNumChildren();
		for (int i = 0; i < n; i++) {
			node.jjtGetChild(i).jjtAccept(this, null);
		}
		return null;
	}
	@Override
	public Object visit(ASTblock node, Object data) {
		int n = node.jjtGetNumChildren();
		for (int i = 0; i < n; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(ASTif_stmt node, Object data) {
		int nChildren = node.jjtGetNumChildren();
		for (int i = 0; i < nChildren; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);   //don't care whether expression, else if or block...
		}
		return null;
	}

	@Override
	public Object visit(ASTelse_if_stmt node, Object data) {
		int nChildren = node.jjtGetNumChildren();
		for (int i = 0; i < nChildren; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);   //don't care what
		}
		return null;
	}

	@Override
	public Object visit(ASTelse_stmt node, Object data) {
		node.jjtGetChild(0).jjtAccept(this, data);
		return null;
	}

	@Override
	//creates symbols and adds them to the symbol table stored in "data"
	public Object visit(ASTdeclaration node, Object data) {
		String directionStr = (String) node.jjtGetValue();
		Variable.Dir dir;
		switch (directionStr) {
		case "input":
			dir = Variable.Dir.INPUT;
			break;
		case "output":
			dir = Variable.Dir.OUTPUT;
			break;
		case "signal":
		default:
			dir = Variable.Dir.INTERNAL;
			break;
		}
		int size = 1;
		int nChildren = node.jjtGetNumChildren();
		for (int i = 0; i < nChildren; i++) {
			SimpleNode current = (SimpleNode) node.jjtGetChild(i);
			if (current instanceof ASTnumber) {
				size = (int) ((ASTnumber) current).jjtGetValue();
			} else { // Id node
				String id = (String) ((ASTid) current).jjtGetValue();
				if (st.containsKey(id)) {
					throw new HDLCheckException("Duplicate identifier '" + id + "'", (SimpleNode) current);
				} else {
					st.put(id, new Variable(dir, size));
				}
			}
		}
		return null;
	}

	@Override
	public Object visit(ASTassignment node, Object data) {
		int nChildren = node.jjtGetNumChildren();
		Signal value = (Signal)node.jjtGetChild(nChildren-1).jjtAccept(this, data);	//calculate expression
		
		//try to assign values from right to left
		int right = 0;
		for (int i = nChildren-2; i >= 0; i--) {
			ASTtarget t = (ASTtarget)node.jjtGetChild(i);
			String name = (String)((ASTid)t.jjtGetChild(0)).value;
			if (st.get(name) == null) throw new HDLCheckException("Signal '" + name +"' undefined.", t);
			if (!st.get(name).writeable()) throw new HDLCheckException("'" + name +"' is readonly.", t);
			
			Signal s = st.get(name).getSignal();
			int t_left, t_right;
			if (t.jjtGetNumChildren() > 1) {  //indices specified
				int a[] = (int[])t.jjtGetChild(1).jjtAccept(this, data);
				if (a.length == 1) {
					t_left = a[0];
					t_right = a[0];
				} else {
					t_left = a[0];
					t_right = a[1];
				}
			} else {  //indices not specified -> use signal bounds
				t_right = 0;
				t_left = s.getWidth()-1;
			}
			int size = t_left>=t_right?t_left-t_right+1:t_right-t_left+1;
			if (right + size > value.getWidth()) throw new HDLCheckException("Right hand side of assignment smaller than left hand side. Right: "+value.getWidth()+".", t);
			// don't execute assignment
			right += size;
		}
		if (right < value.getWidth()) throw new HDLCheckException("Left hand side of assignment smaller than Right hand side. Right: "+value.getWidth()+".", node);
		return null;
	}

	@Override
	public Object visit(ASTtarget node, Object data) {
		assert false;
		return null;
	}

	@Override
	//returns array of indices from left to right (one or two)
	public Object visit(ASTindex node, Object data) {
		int a[] = new int[node.jjtGetNumChildren()];
		for (int i = 0; i < a.length; i++) {
			a[i] = (int)((ASTnumber)node.jjtGetChild(i)).value;
		}
		assert a.length >= 1 && a.length <= 2;
		return a;
	}

	@Override
	public Object visit(ASTexpr node, Object data) {
		Signal value = (Signal)node.jjtGetChild(0).jjtAccept(this, data);	//calculate subexpression
		int nChildren = node.jjtGetNumChildren();
		for (int i = 1; i < nChildren; i++) {
			value = Signal.concat(value,  (Signal)node.jjtGetChild(i).jjtAccept(this, data));
		}
		return value;
	}

	@Override
	public Object visit(ASTsubexpr node, Object data) {
		Signal x = (Signal)node.jjtGetChild(0).jjtAccept(this, data);
		Signal y = (Signal)node.jjtGetChild(1).jjtAccept(this, data);
		Signal z;
		try {
			String op = (String) node.jjtGetValue();
			switch (op) {
			case "==":
				z = Signal.eq(x, y);
				break;
			case "!=":
				z = Signal.ne(x, y);
				break;	
			default:
				assert false;
				z = null;
			}
		} catch (Exception e) {
			throw new HDLCheckException(e, node);
		}
		return z;
	}

	@Override
	public Object visit(ASTterm node, Object data) {
		Signal x = (Signal)node.jjtGetChild(0).jjtAccept(this, data);
		Signal y = (Signal)node.jjtGetChild(1).jjtAccept(this, data);
		Signal z;
		try {
			String op = (String) node.jjtGetValue();
			switch (op) {
			case "+":
				z = Signal.add(x, y);
				break;
			case "-":
				z = Signal.sub(x, y);
				break;	
			case "&":
				z = Signal.and(x, y);
				break;	
			case "|":
				z = Signal.or(x, y);
				break;	
			case "^":
				z = Signal.xor(x, y);
				break;	
			default:
				assert false;
				z = null;
			}
		} catch (Exception e) {
			throw new HDLCheckException(e, node);
		}
		return z;
	}

	@Override
	public Object visit(ASTsubterm node, Object data) {
		Signal x = (Signal)node.jjtGetChild(0).jjtAccept(this, data);
		int nChildren = node.jjtGetNumChildren();
		if (nChildren > 1) {  //has index expression
			int left, right;
			int a[] = (int[])node.jjtGetChild(1).jjtAccept(this, data);
			if (a.length == 1) {
				left = a[0];
				right = a[0];
			} else {
				left = a[0];
				right = a[1];
			}
			try {
				x = Signal.slice(x, left, right);
			} catch (Exception e) {
				throw new HDLCheckException(e, (SimpleNode)node.jjtGetChild(1));
			}
		}
		return x;
	}

	@Override
	public Object visit(ASTid node, Object data) {
		
		String name = (String)node.jjtGetValue();
		if (st.get(name) == null) throw new HDLCheckException("Signal '" + name +"' undefined.", node);
		return st.get(name).getSignal();
	}

	@Override
	public Object visit(ASTnumber node, Object data) {
		int n = (int)node.jjtGetValue();
		if (n == 0) n = 1;
		return Signal.zeros((int)(Math.log(n)/Math.log(2))+1);
	}


	@Override
	public Object visit(ASThex node, Object data) {
		String hexStr = (String)node.jjtGetValue();
		return Signal.zeros(hexStr.length()*4);
	}

	@Override
	public Object visit(ASTbin node, Object data) {
		String binStr = (String)node.jjtGetValue();
		return Signal.zeros(binStr.length());
	}

	@Override
	public Object visit(ASTset_label node, Object data) {
		// do nothing here
		return null;
	}
	
	

}
