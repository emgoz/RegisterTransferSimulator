/* Generated By:JJTree: Do not edit this line. ASTnumber.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package memparser;

public
class ASTnumber extends SimpleNode {
  public ASTnumber(int id) {
    super(id);
  }

  public ASTnumber(MemParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(MemParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ac5be1b55e9288205ab15b4087ae50d5 (do not edit this line) */
