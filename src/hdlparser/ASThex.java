/* Generated By:JJTree: Do not edit this line. ASThex.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package hdlparser;

public
class ASThex extends SimpleNode {
  public ASThex(int id) {
    super(id);
  }

  public ASThex(HDLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HDLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4e68019fd4ff71b00881918698e6fe59 (do not edit this line) */