/* Generated By:JJTree: Do not edit this line. ASTblock.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package hdlparser;

public
class ASTblock extends SimpleNode {
  public ASTblock(int id) {
    super(id);
  }

  public ASTblock(HDLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HDLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=334bc4371ef129714688b7141010ea90 (do not edit this line) */