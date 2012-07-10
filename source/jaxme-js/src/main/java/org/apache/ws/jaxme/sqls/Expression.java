package org.apache.ws.jaxme.sqls;

import org.apache.ws.jaxme.sqls.impl.ExpressionImpl;


/** Interface of an arithmetic expression.
 */
public interface Expression extends Parts {
	/** An expression: The sum of its parts.
	 */
	public static final Type SUM = new ExpressionImpl.TypeImpl("SUM");
	/** An expression: The product of its parts.
	 */
	public static final Type PRODUCT = new ExpressionImpl.TypeImpl("PRODUCT");
	/** An expression: The difference of its two parts.
	 */
	public static final Type DIFFERENCE = new ExpressionImpl.TypeImpl("DIFFERENCE");
	/** An expression: The quotient of its two parts.
	 */
	public static final Type QUOTIENT = new ExpressionImpl.TypeImpl("QUOTIENT");

	/** <p>The type of a boolean constraint.</p>
	 */
	public interface Type extends SQLFactory.Ident {
	}

	/** Returns the expression type.
	 */
	public Type getType();
}
