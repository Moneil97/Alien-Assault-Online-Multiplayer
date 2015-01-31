import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.stream.Stream;


public class CustomOutputStream {

	private OutputStream baseStream;
	private ByteArrayOutputStream bos;
	private ObjectOutputStream oos;

	public CustomOutputStream(OutputStream baseStream) {
		//FileOutputStream fos = new FileOutputStream(new File("test.dat"));
		try {
			this.baseStream = baseStream;
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendObject(Object o){
		try {
			oos.writeObject(o);
//			say("Size: " + bos.size() + " bytes");
			bos.writeTo(baseStream);
			bos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeALL(){
		try {
			oos.close();
			bos.close();
			baseStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void say(Object o) {
		System.out.println(o);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
