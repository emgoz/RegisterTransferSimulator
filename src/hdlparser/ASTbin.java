/* Generated By:JJTree: Do not edit this line. ASTbin.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package hdlparser;

public
class ASTbin extends SimpleNode {
  public ASTbin(int id) {
    super(id);
  }

  public ASTbin(HDLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HDLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c31485641e9683e93cd9c536189e3d76 (do not edit this line) */