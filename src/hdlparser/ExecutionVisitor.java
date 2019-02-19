package hdlparser;

import java.util.HashMap;

import simulationEngine.Signal;
import symbols.LabelStr;
import symbols.Symbol;

public class ExecutionVisitor implements HDLParserVisitor {

	// Extends RuntimeException instead of Exception as the visitor interface is
	// created by JJtree without "throws"
	// Therefore, the caller of the visitor has to handle these kinds of
	// exceptions carefully!
	public class ExecutionException extends RuntimeException {
		public ExecutionException(String s, SimpleNode n) {
			super("At line " + n.jjtGetFirstToken().beginLine + ", column " + n.jjtGetFirstToken().beginColumn + ": " + s);
		}
		public ExecutionException(Exception e, SimpleNode n) {  //Repack
			super("At line " + n.jjtGetFirstToken().beginLine + ", column " + n.jjtGetFirstToken().beginColumn + ": " + e.getMessage());
		}
		public ExecutionException() {
		}
	}

	@Override
	public Object visit(SimpleNode node, Object data) {
		assert false;
		return null;
	}
	/**
	 * Start node
	 * Gets ST passed in "data"
	 */
	@Override
	public Object visit(ASTfile node, Object data) {
		int n = node.jjtGetNumChildren();
		for (int i = 0; i < n; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
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
		//evaluate expr
		Signal condition = (Signal)node.jjtGetChild(0).jjtAccept(this, data);	//calculate expression
		int nChildren = node.jjtGetNumChildren();
		if (condition.nonzero()) {
			node.jjtGetChild(1).jjtAccept(this, data);  //execute block
		} else if (nChildren > 2) {
			for (int i = 2; i < nChildren; i++) {
				boolean executed = (boolean)node.jjtGetChild(i).jjtAccept(this, data);
				if (executed) break;  //only try other options if elseif false
			}
		}
		return null;
	}

	@Override
	//returns whether executed or not
	public Object visit(ASTelse_if_stmt node, Object data) {
		Signal condition = (Signal)node.jjtGetChild(0).jjtAccept(this, data);	//calculate expression
		if (condition.nonzero()) {
			node.jjtGetChild(1).jjtAccept(this, data);  //execute block
			return true;
		} else {
			return false;
		}
	}

	@Override
	//returns whether executed or not
	public Object visit(ASTelse_stmt node, Object data) {
		node.jjtGetChild(0).jjtAccept(this, data);
		return true;
	}

	@Override
	public Object visit(ASTdeclaration node, Object data) {
		//don't do anything here, checkVisitor created symbol table
		return null;
	}

	@Override
	public Object visit(ASTassignment node, Object data) {
		int nChildren = node.jjtGetNumChildren();
		Signal value = (Signal)node.jjtGetChild(nChildren-1).jjtAccept(this, data);	//calculate expression
		
		HashMap<String, Symbol> st = (HashMap<String, Symbol>) data;
		
		//assign values from right to left
		int right = 0;
		for (int i = nChildren-2; i >= 0; i--) {
			ASTtarget t = (ASTtarget)node.jjtGetChild(i);
			String name = (String)((ASTid)t.jjtGetChild(0)).value;
			if (st.get(name) == null) assert false;// throw new ExecutionException("Signal '" + name +"' undefined.", t);
			if (!st.get(name).writeable()) assert false;// throw new ExecutionException("'" + name +"' is readonly.", t);
			
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
			if (right + size > value.getWidth()) assert false;//throw new ExecutionException("Right hand side of assignment smaller than left hand side. Right: "+value.getWidth()+".", t);
			try {
				Signal slice = Signal.slice(value, right+size-1, right);
				s.setSlice(slice, t_left, t_right);
			} catch (Exception e) {
				throw new ExecutionException(e, t);
			}
			right += size;
		}
		if (right < value.getWidth()) assert false;//throw new ExecutionException("Left hand side of assignment smaller than Right hand side. Right: "+value.getWidth()+".", node);
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
			throw new ExecutionException(e, node);
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
			throw new ExecutionException(e, node);
		}
		return z;
	}

	@Override
	public Object visit(ASTsubterm node, Object data) {
		boolean negate = (boolean) node.jjtGetValue();
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
				throw new ExecutionException(e, (SimpleNode)node.jjtGetChild(1));
			}
		}
		if (negate) {
			x = Signal.not(x);
		}
		return x;
	}

	@Override
	public Object visit(ASTid node, Object data) {
		HashMap<String, Symbol> st = (HashMap<String, Symbol>) data;
		
		String name = (String)node.jjtGetValue();
		if (st.get(name) == null) assert false; // throw new ExecutionException("Signal '" + name +"' undefined.", node);
		return st.get(name).getSignal();
	}

	@Override
	public Object visit(ASTnumber node, Object data) {
		int n = (int)node.jjtGetValue();
		return new Signal(Integer.toString(n,2));
	}

	@Override
	public Object visit(ASThex node, Object data) {
		String hexStr = (String)node.jjtGetValue();
		String binStr = "";
		for (char c  : hexStr.toCharArray()) {
			if (c == '0') binStr += "0000";
			if (c == '1') binStr += "0001";
			if (c == '2') binStr += "0010";
			if (c == '3') binStr += "0011";
			if (c == '4') binStr += "0100";
			if (c == '5') binStr += "0101";
			if (c == '6') binStr += "0110";
			if (c == '7') binStr += "0111";
			if (c == '8') binStr += "1000";
			if (c == '9') binStr += "1001";
			if (c == 'a' || c == 'A') binStr += "1010";
			if (c == 'b' || c == 'B') binStr += "1011";
			if (c == 'c' || c == 'C') binStr += "1100";
			if (c == 'd' || c == 'D') binStr += "1101";
			if (c == 'e' || c == 'E') binStr += "1110";
			if (c == 'f' || c == 'F') binStr += "1111";
		}
		return new Signal(binStr);
	}

	@Override
	public Object visit(ASTbin node, Object data) {
		String binStr = (String)node.jjtGetValue();
		return new Signal(binStr);
	}
	public Object visit(ASTzeroOrOne node, Object data) {
		String binStr = (String)node.jjtGetValue();
		return new Signal(binStr);
	}

	@Override
	public Object visit(ASTset_label node, Object data) {
		HashMap<String, Symbol> st = (HashMap<String, Symbol>) data;
		
		String text = (String)node.jjtGetValue();
		st.put("#", new LabelStr(text));
		return null;
	}

}
