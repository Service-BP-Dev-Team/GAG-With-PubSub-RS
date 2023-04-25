/*******************************************************************************
 * Copyright (c) 2000, 2008 INRIA.
 * 
 *    This file is part of LogNet project
 *    <a href="http://www-sop.inria.fr/teams/lognet">LogNet</a>
 * 
 *     LogNet is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     LogNet is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 * 
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with LogNet; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *     INRIA
 * 
 *******************************************************************************/
// $Id: VirtPipeMessage.java 2891 2009-07-20 14:01:58Z bboussema $
package inria.lognet.virtpipes;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Message encapsulation for the TCP transmission. INIT type is for passing
 * VirtPipesServices' IDs. Then NORMAL is used for encapsulating User's
 * messages/data.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2891 $
 */
public class VirtPipeMessage {

	public final static byte INIT = 0;
	public final static byte NORMAL = 1;

	public final static ByteBuffer format(final byte type, final UUID channelId) {
		return VirtPipeMessage.format(type, channelId, null);
	}

	/**
	 * Format the VirtPipeMessage's attributes in to a array of bytes with
	 * ByteBuffer facility.
	 * 
	 * @param type
	 * @param channelId
	 * @param buf
	 * @return
	 */
	public final static ByteBuffer format(final byte type,
			final UUID channelId, final ByteBuffer buf) {
		int size;
		if (buf != null) {
			size = buf.limit();
		} else {
			size = 0;
		}
		final ByteBuffer byteBuffer = ByteBuffer.allocate(1 + 16 + 4 + size);
		byteBuffer.put(type);
		byteBuffer.putLong(channelId.getMostSignificantBits());
		byteBuffer.putLong(channelId.getLeastSignificantBits());
		byteBuffer.putInt(size);
		if (buf != null) {
			byteBuffer.put(buf);
			buf.position(0);
		}
		byteBuffer.flip();
		return byteBuffer;
	}

	/**
	 * Read an array of bytes into a VirtPipeMessage, from multiple ByteBuffers.
	 * It can complete a already create VirtPipeMessage in argument and return a
	 * VirtPipeMessage completed or null if not complete.
	 * 
	 * @param byteBuffers
	 * @param arg
	 * @return
	 */
	public static VirtPipeMessage readVirtPipeMessage(
			final List<ByteBuffer> byteBuffers, final VirtPipeMessage arg) {
		final Map<ByteBuffer, Integer> recover = new HashMap<ByteBuffer, Integer>();
		final List<ByteBuffer> toDelete = new LinkedList<ByteBuffer>();
		Byte type = null;
		Long most = null;
		Long least = null;
		VirtPipeMessage msg = arg;

		try {
			for (final ByteBuffer byteBuffer : byteBuffers) {
				recover.put(byteBuffer, byteBuffer.position());
				if (msg == null && type == null) {
					type = byteBuffer.get();
				}
				if (msg == null && most == null) {
					most = byteBuffer.getLong();
				}
				if (msg == null && least == null) {
					least = byteBuffer.getLong();
				}
				if (msg == null && type != null && most != null
						&& least != null) {
					final int size = byteBuffer.getInt();
					msg = new VirtPipeMessage(type, new UUID(most, least), size);
				}
				if (msg != null) {
					msg.append(byteBuffer);
				}
				if (byteBuffer.remaining() == 0) {
					toDelete.add(byteBuffer);
				}
				if (msg != null && msg.getBuf().remaining() == 0) {
					break;
				}
			}
			for (final ByteBuffer byteBuffer : toDelete) {
				byteBuffers.remove(byteBuffer);
			}
		} catch (final BufferUnderflowException e) {
			int size = 0;
			for (final ByteBuffer byteBuffer : byteBuffers) {
				if (recover.containsKey(byteBuffer)) {
					byteBuffer.position(recover.get(byteBuffer));
				}
				size += byteBuffer.remaining();
			}
			if (byteBuffers.size() > 1) {
				final ByteBuffer newBuf = ByteBuffer.allocate(size);
				for (final ByteBuffer byteBuffer : byteBuffers) {
					newBuf.put(byteBuffer);
				}
				byteBuffers.clear();
				byteBuffers.add(newBuf);
				return VirtPipeMessage.readVirtPipeMessage(byteBuffers, arg);
			}
			return null;
		}
		Activator.LOGGER.fine("VirtPipeMessage remaining "
				+ msg.getBuf().remaining() + " (" + msg.getBuf().position()
				+ " / " + msg.getBuf().capacity() + ")");
		return msg;
	}
	private final UUID channelId;
	private final byte type;
	private final ByteBuffer buf;

	public VirtPipeMessage(final byte type, final UUID channelId,
			final ByteBuffer buf) {
		this.type = type;
		if (channelId == null) {
			throw new IllegalArgumentException("channelId is null");
		}
		this.channelId = channelId;
		this.buf = buf;
	}

	public VirtPipeMessage(final byte type, final UUID channelId, final int size) {
		this.type = type;
		if (channelId == null) {
			throw new IllegalArgumentException("channelId is null");
		}
		this.channelId = channelId;
		buf = ByteBuffer.allocate(size);
	}

	private void append(final ByteBuffer byteBuffer) {
		try {
			buf.put(byteBuffer);
		} catch (final BufferOverflowException e) {
			final byte[] tmp = new byte[buf.remaining()];
			byteBuffer.get(tmp);
			buf.put(tmp);
		}
	}

	public ByteBuffer format() {
		return VirtPipeMessage.format(type, channelId, buf);
	}

	public ByteBuffer getBuf() {
		return buf;
	}

	public final UUID getChannelId() {
		return channelId;
	}

	public final byte getType() {
		return type;
	}

}
