/* Generated By:JJTree: Do not edit this line. ASTelse_stmt.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package hdlparser;

public
class ASTelse_stmt extends SimpleNode {
  public ASTelse_stmt(int id) {
    super(id);
  }

  public ASTelse_stmt(HDLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HDLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8db55bf4bc4c6a4ff55a32fc794a067a (do not edit this line) */
