/* Generated By:JJTree: Do not edit this line. ASTfile.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package memparser;

public
class ASTfile extends SimpleNode {
  public ASTfile(int id) {
    super(id);
  }

  public ASTfile(MemParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MemParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=251337acef49139a2ce68499b53e5e3c (do not edit this line) */