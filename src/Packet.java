import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Packet {

	private InetAddress address;
	private int port;
	private boolean ping;
	private long timestamp;
	private long pingtime;
	private double signal;
	private boolean returningping = false;

	public Packet(InetAddress address, int port, long timestamp, double signal) {
		this.address = address;
		this.port = port;
		this.ping = false;
		this.signal = signal;
		this.timestamp = timestamp;
	}

	public Packet(InetAddress address, int port, long timestamp, long pingtime) {
		this.address = address;
		this.port = port;
		this.ping = true;
		this.pingtime = pingtime;
		this.timestamp = timestamp;
	}

	public boolean isPing() {
		return ping;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public double getSignal() {
		return signal;
	}

	public long getPing() {
		return pingtime;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setReturningPing() {
		returningping = true;
	}

	public boolean isReturningPing() {
		return returningping;
	}

	public DatagramPacket toDatagramPacket() {
		ByteBuffer buffer = ByteBuffer.allocate(17);
		if (ping) {
			if (returningping) {
				buffer.put((byte) 2);
			} else {
				buffer.put((byte) 1);
			}
			buffer.putLong(1, timestamp);
			buffer.putLong(9, pingtime);
		} else {
			buffer.put((byte) 0);
			buffer.putLong(1, timestamp);
			buffer.putDouble(9, signal);
		}
		return new DatagramPacket(buffer.array(), 17, address, port);
	}

	public static Packet fromDatagramPacket(DatagramPacket dp) {
		ByteBuffer buffer = ByteBuffer.wrap(dp.getData());
		if (buffer.get(0) == (byte) 0) {
			return new Packet(dp.getAddress(), dp.getPort(), buffer.getLong(1),
					buffer.getDouble(9));
		} else {
			Packet pingPacket = new Packet(dp.getAddress(), dp.getPort(),
					buffer.getLong(1), buffer.getLong(9));
			if (buffer.get(0) == (byte) 2) {
				pingPacket.setReturningPing();
			}
			return pingPacket;
		}
	}

	public void send(DatagramSocket ds) throws IOException {
		ds.send(toDatagramPacket());
	}

	public static Packet recieve(DatagramSocket ds) throws IOException {
		byte[] dataArray = new byte[1024];
		DatagramPacket packet = new DatagramPacket(dataArray, dataArray.length);
		ds.receive(packet);
		return Packet.fromDatagramPacket(packet);
	}

}
