package com.alrex.throwability.common.capability;

public enum ThrowType {
	One_As_Item(0), All_As_Item(1), One_As_Entity(2);
	public byte code;

	ThrowType(int code) {
		this.code = (byte) code;
	}

	public static ThrowType fromCode(byte code) {
		switch (code) {
			case 0:
				return One_As_Item;
			case 1:
				return All_As_Item;
			case 2:
				return One_As_Entity;
		}
		return One_As_Item;
	}

	public byte getCode() {
		return code;
	}
}
