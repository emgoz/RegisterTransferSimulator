package memparser;

import simulationEngine.Memory;

public class MemoryVisitor implements MemParserVisitor {

	@Override
	public Object visit(SimpleNode node, Object data) {
		assert false;
		return null;
	}
	/**
	 * Data = Memory Object
	 */
	@Override
	public Object visit(ASTfile node, Object data) {
		int nChildren = node.jjtGetNumChildren();
		for (int i = 0; i < nChildren; i++) {
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(ASTdefault_stmt node, Object data) {
		String value = (String)((ASTnumber) node.jjtGetChild(0)).jjtGetValue();
		Memory mem = (Memory)data;
		mem.setDefault(value);		
		return null;
	}

	@Override
	public Object visit(ASTaddr_stmt node, Object data) {
		String addr = (String)((ASTnumber) node.jjtGetChild(0)).jjtGetValue();
		String value = (String)((ASTnumber) node.jjtGetChild(1)).jjtGetValue();
		Memory mem = (Memory)data;
		mem.addValue(addr, value);	
		boolean readOnly = (boolean) node.value;
		if (readOnly) {
			mem.setReadOnly(addr);
		}
		return null;
	}

	@Override
	public Object visit(ASTnumber node, Object data) {
		assert false;
		return null;
	}

}
