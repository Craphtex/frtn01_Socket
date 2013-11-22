import java.net.InetAddress;

public abstract class Glue {

	protected double transferData;
	protected double localData;
	protected InetAddress address;
	protected int port;
	protected long ping;
	protected long lastPacket;
	protected Packet returningPingPacket = null;

	public synchronized double getData() {
		return localData;
	}

	public synchronized void setData(double s) {
		transferData = s;
	}

	public synchronized Packet getPacket() {
		if (address == null || port < 0)
			return null;
		long time = System.currentTimeMillis();
//		Util.print("Sending  (Glue): " + time + " " + transferData);
		return new Packet(address, port, time, transferData);
	}

	public synchronized Packet getPingPacket() {
		if (address == null || port < 0)
			return null;
		long time = System.currentTimeMillis();
		Util.print("Sending  (Glue): " + time + " Ping");
		return new Packet(address, port, time, time);
	}

	public synchronized Packet getReturningPingPacket() {
		Packet packet = returningPingPacket;
		returningPingPacket = null;
		if (packet != null) {
			Util.print("Sending  (Glue): " + System.currentTimeMillis()
					+ " Returning ping");
		}
		return packet;
	}

	public synchronized void setPacket(Packet packet) {
		long time = System.currentTimeMillis();
		if (packet.isPing()) {
			if (packet.isReturningPing()) {
				ping = time - packet.getPing();
				Util.print("Received (Glue): " + time + " Ping [" + ping + "]");
			} else {
				packet.setReturningPing();
				returningPingPacket = packet;
				Util.print("Received (Glue): " + time + " Ping packet ["
						+ packet.isReturningPing() + "ms]");
			}
		} else {
			if (packet.getTimestamp() > lastPacket) {
				localData = packet.getSignal();
				//Util.print("Received (Glue): " + time + " " + localData);
			} else {
				Util.print("Received (Glue): " + time + " discarded old data");
			}
		}
	}

	public synchronized InetAddress getAddress() {
		return address;
	}

	public synchronized long getPing() {
		return ping;
	}
}
