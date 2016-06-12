package com.showjoy.tashow.exception;

/**
 * 业务验证相关的异常.
 */
public class BizValidateException extends BizException {

	/**
	 * <code>serialVersionUID</code>.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 默认异常构造器.
	 */
	public BizValidateException() {
		super();
	}

	/**
	 * 根据异常信息和原生异常构造对象.
	 * 
	 * @param message
	 *            异常信息.
	 * @param cause
	 *            原生异常.
	 */
	public BizValidateException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * 根据异常信息构造对象.
	 * 
	 * @param message
	 *            异常信息.
	 */
	public BizValidateException(final String message) {
		super(message);
	}

	/**
	 * 根据原生异常构造对象.
	 * 
	 * @param cause
	 *            原生异常.
	 */
	public BizValidateException(final Throwable cause) {
		super(cause);
	}
}
