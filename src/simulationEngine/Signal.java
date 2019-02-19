package simulationEngine;

public class Signal {
	char value[];
	
	public static void main(String args[]) throws Exception {
		Signal b = new Signal("101010");
		System.out.println(b.getDecString());
	}
	public Signal(int width) {
		this.value = new char[width];
	}
	public Signal(char value[]) {
		this.value = value.clone();
	}
	public static Signal zeros(int width) {
		Signal r = new Signal(width);
		for (int i = 0; i < width; i++) {
			r.value[i] = '0';
		}
		return r;
	}
	public static Signal ones(int width) {
		Signal r = new Signal(width);
		for (int i = 0; i < width; i++) {
			r.value[i] = '1';
		}
		return r;
	}
	public Signal(String value) {
		this.value = new char[value.length()];
		for (int i = 0; i < value.length(); i++) {
			this.value[i] = value.charAt(value.length()-1-i);
		}
	}
	public Signal(String value, int width) {
		this.value = new char[width];
		for (int i = 0; i < width; i++) {
			this.value[i] = i<value.length()?value.charAt(value.length()-1-i):'0';
		}
	}
	
	public int getWidth() {
		return this.value.length;
	}
	public char[] getValue() {
		return value;
	}
	public void setValue(char value[]) throws Exception {
		if (this.value.length != value.length) throw new LogicException("Signal assignment with different sizes");
		this.value = value.clone();
	}
	public void setSame(Signal other) throws Exception {
		if (this.value.length != value.length) throw new LogicException("Signal assignment with different sizes");
		this.value = other.value.clone();
	}
	public String toString() {
		String r = "";
		for (int i = 0; i < this.value.length; i++) {
			r = this.value[i] + r;
		}
		return r;
	}
	public boolean equals(Signal x) {
		if (this.value.length != x.value.length) {
			return false;  //rather rise exception?? TODO
		}
		for (int i = 0; i < x.value.length; i++) {
			if (this.value[i] != x.value[i]) return false;
		}
		return true;
	}
	public boolean nonzero() {
		for (int i = 0; i < value.length; i++) {
			if (this.value[i] != '0') return true;
		}
		return false;
	}
	public void setSlice(Signal x, int left, int right) throws Exception {
		int w = this.value.length;
		int dir = left >= right?1:-1;
		int u = left >= right? left-right+1 : right-left+1;
		if (x.value.length != u) throw new LogicException("Index interval of slice assignments do not match signal length.");
		if (left < 0 || left >= w) throw new LogicException("Left limit of slice assignment out of bounds: "+left);
		if (right < 0 || right >= w) throw new LogicException("Right limit of slice assignment out of bounds: "+right);
		for (int i = 0, j = right; i < u; i++, j += dir){
			this.value[j] = x.value[i];
		}
	}
	public static Signal slice(Signal x, int left, int right) throws Exception {
		int w = x.value.length;
		int dir = left >= right?1:-1;
		int u = left >= right? left-right+1 : right-left+1;
		if (left < 0 || left >= w) throw new LogicException("Left limit of slice operator out of bounds: "+left);
		if (right < 0 || right >= w) throw new LogicException("Right limit of slice operator out of bounds: "+right);
		char v[] = new char[u];
		for (int i = 0, j = right; i < u; i++, j += dir){
			v[i] = x.value[j];
		}
		return new Signal(v);
	}
	public static Signal select(Signal x, int index) throws Exception {
		int w = x.value.length;
		if (index < 0 || index >= w) throw new LogicException("Index of bit vector out of bounds: "+index);
		return new Signal(new char[]{x.value[index]});
	}
	public static Signal concat(Signal x, Signal y) {
		char[] v = new char[x.value.length + y.value.length];
		for (int i = 0; i < y.value.length; i ++) {
			v[i] = y.value[i];
		}
		for (int i = 0; i < x.value.length; i ++) {
			v[y.value.length+i] = x.value[i];
		}
		return new Signal(v);
	}
	public static Signal or(Signal x, Signal y) throws Exception {
		if (x.value.length != y.value.length) {
			throw new LogicException("Bit vector sizes of 'OR' operation do not match.");
		}
		int w = x.value.length;
		char v[] = new char[w];
		for (int i = 0; i < w; i++){
			switch ("" + x.value[i] + y.value[i]) {
			case "00": v[i] = '0'; break;
			case "01": v[i] = '1'; break;
			case "10": v[i] = '1'; break;
			case "11": v[i] = '1'; break;
			}
		}
		return new Signal(v);
	}
	public static Signal and(Signal x, Signal y) throws Exception {
		if (x.value.length != y.value.length) {
			throw new LogicException("Bit vector sizes of 'AND' operation do not match.");
		}
		int w = x.value.length;
		char v[] = new char[w];
		for (int i = 0; i < w; i++){
			switch ("" + x.value[i] + y.value[i]) {
			case "00": v[i] = '0'; break;
			case "01": v[i] = '0'; break;
			case "10": v[i] = '0'; break;
			case "11": v[i] = '1'; break;
			}
		}
		return new Signal(v);
	}
	public static Signal xor(Signal x, Signal y) throws Exception {
		if (x.value.length != y.value.length) {
			throw new LogicException("Bit vector sizes of 'XOR' operation do not match.");
		}
		int w = x.value.length;
		char v[] = new char[w];
		for (int i = 0; i < w; i++){
			switch ("" + x.value[i] + y.value[i]) {
			case "00": v[i] = '0'; break;
			case "01": v[i] = '1'; break;
			case "10": v[i] = '1'; break;
			case "11": v[i] = '0'; break;
			}
		}
		return new Signal(v);
	}
	public static Signal not(Signal x) {
		int w = x.value.length;
		char v[] = new char[w];
		for (int i = 0; i < w; i++){
			v[i] = x.value[i] == '0'?'1':'0';
		}
		return new Signal(v);
	}
	public static Signal eq(Signal x, Signal y) throws Exception {
		if (x.value.length != y.value.length) {
			throw new LogicException("Bit vector sizes of '==' operation do not match.");
		}
		for (int i = 0; i < x.value.length; i++) {
			if (x.value[i] != y.value[i]) return new Signal(new char[]{'0'});
		}
		return new Signal(new char[]{'1'});
	}
	public static Signal ne(Signal x, Signal y) throws Exception {
		if (x.value.length != y.value.length) {
			throw new LogicException("Bit vector sizes of '!=' operation do not match.");
		}
		for (int i = 0; i < x.value.length; i++) {
			if (x.value[i] != y.value[i]) return new Signal(new char[]{'1'});
		}
		return new Signal(new char[]{'0'});
	}
	public static Signal add(Signal x, Signal y) {
		int w = x.value.length;
		if (w < y.value.length) {
			w = y.value.length;
		}
		w++;
		char[] v = new char[w];
		char c = '0';
		for (int i = 0; i < w; i++){
			char a = '0';
			char b = '0';
			if (i < x.value.length) a = x.value[i];
			if (i < y.value.length) b = y.value[i];
			switch (""+a+b+c) {
			case "000": v[i] = '0'; c = '0'; break;
			case "001": v[i] = '1'; c = '0'; break;
			case "010": v[i] = '1'; c = '0'; break;
			case "011": v[i] = '0'; c = '1'; break;
			case "100": v[i] = '1'; c = '0'; break;
			case "101": v[i] = '0'; c = '1'; break;
			case "110": v[i] = '0'; c = '1'; break;
			case "111": v[i] = '1'; c = '1'; break;
			}
		}
		return new Signal(v);
	}
	public static Signal sub(Signal x, Signal y) throws Exception {
		int w = x.value.length;
		if (w < y.value.length) {
			w = y.value.length;
		}
		w++;
		char[] v = new char[w];
		char c = '0';
		for (int i = 0; i < w; i++){
			char a = '0';
			char b = '0';
			if (i < x.value.length) a = x.value[i];
			if (i < y.value.length) b = y.value[i];
			switch (""+a+b+c) {
			case "000": v[i] = '0'; c = '0'; break;
			case "001": v[i] = '1'; c = '1'; break;
			case "010": v[i] = '1'; c = '1'; break;
			case "011": v[i] = '0'; c = '1'; break;
			case "100": v[i] = '1'; c = '0'; break;
			case "101": v[i] = '0'; c = '0'; break;
			case "110": v[i] = '0'; c = '0'; break;
			case "111": v[i] = '1'; c = '1'; break;
			}
		}
		return new Signal(v);
	}
	public String getTruncatedString() {
		String r = "";
		boolean zf = false;
		for (int j = getWidth()-1; j >= 0; j--) {
			if (value[j] == '0' && (zf || j == 0)) {
				r += '0';
			} else if (value[j] == '1'){
				r += '1';
				zf = true;
			}
		}
		return r;
	}
	public String getBinString() {
		String r = "";
		for (int j = getWidth()-1; j >= 0; j--) {
			if (value[j] == '0') {
				r += '0';
			} else if (value[j] == '1'){
				r += '1';
			} else {
				return "ERROR";
			}
		}
		return r;
	}
	public String getHexString() {
		String r = "";
		for (int j = 0; j < getWidth(); j+= 4) {
			String s = ""+safeGetBit(j+3)+safeGetBit(j+2)+safeGetBit(j+1)+safeGetBit(j);
			//unoptimized:
			try {
				int decimal = Integer.parseInt(s,2);
				r = Integer.toString(decimal,16)+r;
			} catch (NumberFormatException e) {
				return "ERROR";
			} 
		}
		return "$"+r;
	}
	public String getDecString() {
		long l = 0;
		for (int j = getWidth()-1; j >= 0; j--) {
			l <<= 1;
			char bit = safeGetBit(j);
			if (bit == '0' || bit ==  '1') {
				l |= safeGetBit(j)=='1'?1:0;
			} else {
				return "ERROR";
			}
		}
		return ""+l;
	}
	public String getString(int base) {
		switch (base) {
		case 2:
			return getBinString();
		case 10:
			return getDecString();
		case 16:
			return getHexString();
		default:
			return "ERROR";
		}
	}
	private char safeGetBit(int j) {
		if (j < getWidth()) return value[j];
		else return '0';
	}
}
