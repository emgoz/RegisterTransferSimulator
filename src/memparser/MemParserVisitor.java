/* Generated By:JavaCC: Do not edit this line. MemParserVisitor.java Version 5.0 */
package memparser;

public interface MemParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTfile node, Object data);
  public Object visit(ASTdefault_stmt node, Object data);
  public Object visit(ASTaddr_stmt node, Object data);
  public Object visit(ASTnumber node, Object data);
}
/* JavaCC - OriginalChecksum=629400018adfdf562821333150309c5b (do not edit this line) */
