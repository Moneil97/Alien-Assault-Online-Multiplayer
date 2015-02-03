import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("serial")
public class MyPacket implements Serializable{

	//Do not put a Serial Version
	private ServerEnums purpose;
	private Object[] objects;

	public MyPacket(ServerEnums purpose, Object ... objects) {
		this.purpose = purpose;
		this.objects = objects;
	}
	
	public ServerEnums getPurpose(){
		return purpose;
	}
	
	public Object[] getObjects(){
		return objects;
	}
	
	public String toString(){
		String output = "";
		
		output += "Purpose: " + purpose;
		output += "\nObjects: " + Arrays.toString(objects);
		
//		for (Field m : this.getClass().getDeclaredFields())
			
		
		
		return output;
	}
	
//	public static void main (String args[]){
//		System.out.println(Arrays.toString(new MyPacket(ServerEnums.blank).getClass().getDeclaredFields()));
//	}

}
