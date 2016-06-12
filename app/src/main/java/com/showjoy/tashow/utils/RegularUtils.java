package com.showjoy.tashow.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.text.TextUtils;

import com.showjoy.tashow.exception.BizValidateException;

/**
 * 验证工具类
 *
 * @author Lucy
 *
 */
public class RegularUtils {
	public static boolean checkName(String name) {
		if (TextUtils.isEmpty(name)) {
			throw new BizValidateException("昵称不能为空");
		}
		if (name.length() < 3 || name.length() > 16 || !nameFormat(name)) {
			throw new BizValidateException("昵称不符合规范，3-16个中英文字符、数字");
		}
		return true;
	}

	public static boolean checkEmail(Activity context, String email) {
		if (TextUtils.isEmpty(email)) {
			throw new BizValidateException("邮箱不能为空");
		}
		if (!emailFormat(email) || email.length() > 31) {
			throw new BizValidateException("邮箱格式不正确");
		}
		return true;
	}

	public static boolean checkPhone(String phone) {
		if (TextUtils.isEmpty(phone)) {
			throw new BizValidateException("手机号码不能为空");
		}
		if (!phoneFormat(phone)) {
			throw new BizValidateException("手机号码格式不正确");
		}
		return true;
	}

	public static boolean checkCode(String code) {
		if (TextUtils.isEmpty(code)) {
			throw new BizValidateException("验证码不能为空");
		}
		return true;
	}

	public static boolean checkPassword(Activity context, String password) {
		if (TextUtils.isEmpty(password)) {
			throw new BizValidateException("密码不能为空");
		}
		if (!passwordFormat(password)) {
			throw new BizValidateException("密码格式是6-16位英文字符、数字");
		}
		return true;
	}

	public static boolean checkPassword(Activity context, String password, String confirm) {
		if (TextUtils.isEmpty(password)) {
			throw new BizValidateException("密码不能为空");
		}
		if (!checkPassword(context, password)) {
			return false;
		}
		if (!password.equals(confirm)) {
			throw new BizValidateException("两次密码设置不一致");
		}
		return true;
	}

	public static boolean checkCode(Activity context, String code) {
		if (TextUtils.isEmpty(code)) {
			throw new BizValidateException("验证码不能为空");
		}
		if (code.length() != 6) {
			throw new BizValidateException("请输入正确的六位验证码");
		}
		return true;
	}

	public static boolean check(Activity context, String email, String password) {
		if (TextUtils.isEmpty(email)) {
			throw new BizValidateException("邮箱不能为空");
		}
		if (!emailFormat(email) || email.length() > 31) {
			throw new BizValidateException("邮箱格式不正确");
		}
		if (!checkPassword(context, password)) {
			return false;
		}
		return true;
	}

	private static boolean emailFormat(String email) {
		Pattern pattern = Pattern.compile("^[A-Za-z\\d]+(\\.[A-Za-z\\d]+)*@([\\dA-Za-z](-[\\dA-Za-z])?)+(\\.{1,2}[A-Za-z]+)+$");
		Matcher mc = pattern.matcher(email);
		return mc.matches();
	}

	/**
	 * 以字母开头，长度在3~16之间，只能包含字符、数字和下划线（w）
	 * 
	 * @param password
	 * @return
	 */
	private static boolean passwordFormat(String password) {
		Pattern pattern = Pattern.compile("^[\\@A-Za-z0-9\\!\\#\\$\\%\\^\\&\\*\\.\\~]{6,16}$");
		Matcher mc = pattern.matcher(password);
		return mc.matches();
	}

	public static boolean nameFormat(String name) {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5A-Za-z0-9_]{3,16}$");
		Matcher mc = pattern.matcher(name);
		return mc.matches();
	}

	public static boolean phoneFormat(String phone) {
		Pattern pattern = Pattern.compile("[1][358]\\d{9}");
		Matcher mc = pattern.matcher(phone);
		return mc.matches();
	}

	/**
	 * 获取含双字节字符的字符串字节长度
	 * 
	 * @param s
	 * @return
	 */
	public static int getStringLength(String s) {
		char[] chars = s.toCharArray();
		int count = 0;
		for (char c : chars) {
			count += getSpecialCharLength(c);
		}
		return count;
	}

	/**
	 * 获取字符长度：汉、日、韩文字符长度为2，ASCII码等字符长度为1
	 * 
	 * @param c
	 *            字符
	 * @return 字符长度
	 */
	private static int getSpecialCharLength(char c) {
		if (isLetter(c)) {
			return 1;
		} else {
			return 2;
		}
	}

	/**
	 * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
	 * 
	 * @param c, 需要判断的字符
	 * @return boolean, 返回true,Ascill字符
	 */
	private static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}
}
