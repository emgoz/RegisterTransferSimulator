/* Generated By:JJTree: Do not edit this line. ASTfile.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package hdlparser;

public
class ASTfile extends SimpleNode {
  public ASTfile(int id) {
    super(id);
  }

  public ASTfile(HDLParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(HDLParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3e58842c7554cea038f4a69e57f1fedc (do not edit this line) */
