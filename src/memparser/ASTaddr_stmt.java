/* Generated By:JJTree: Do not edit this line. ASTaddr_stmt.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package memparser;

public
class ASTaddr_stmt extends SimpleNode {
  public ASTaddr_stmt(int id) {
    super(id);
  }

  public ASTaddr_stmt(MemParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MemParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=43b776f7036d75d950222e7dc0211b8f (do not edit this line) */