/* Generated By:JavaCC: Do not edit this line. HDLParserVisitor.java Version 5.0 */
package hdlparser;

public interface HDLParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTfile node, Object data);
  public Object visit(ASTblock node, Object data);
  public Object visit(ASTif_stmt node, Object data);
  public Object visit(ASTelse_if_stmt node, Object data);
  public Object visit(ASTelse_stmt node, Object data);
  public Object visit(ASTdeclaration node, Object data);
  public Object visit(ASTassignment node, Object data);
  public Object visit(ASTtarget node, Object data);
  public Object visit(ASTindex node, Object data);
  public Object visit(ASTexpr node, Object data);
  public Object visit(ASTsubexpr node, Object data);
  public Object visit(ASTterm node, Object data);
  public Object visit(ASTsubterm node, Object data);
  public Object visit(ASTid node, Object data);
  public Object visit(ASTnumber node, Object data);
  public Object visit(ASThex node, Object data);
  public Object visit(ASTbin node, Object data);
  public Object visit(ASTset_label node, Object data);
}
/* JavaCC - OriginalChecksum=b88a8d27497230faab2682c3f0a3c87a (do not edit this line) */
