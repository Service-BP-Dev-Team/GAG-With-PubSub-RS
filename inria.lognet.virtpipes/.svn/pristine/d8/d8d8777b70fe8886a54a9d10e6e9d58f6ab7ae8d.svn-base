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
// $Id: SelectableVirtPipeOutput.java 2891 2009-07-20 14:01:58Z bboussema $
package inria.lognet.virtpipes;

import inria.lognet.channels.ChannelStrategy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.UUID;

/**
 * Other end of SelectableVirtPipeInput, you send data as a stream not messages.
 * You need to be sure that only one Output talks to only one Input. You write
 * information with your own thread. The other part of the pipe is dealt with
 * ChannelsPool.
 * 
 * @author Baptiste.Boussemart@sophia.inria.fr
 * @version $Revision: 2891 $
 */
public class SelectableVirtPipeOutput {

	private final UUID id;

	private final VirtPipesService virtPipesService;

	private final Pipe pipe;

	public SelectableVirtPipeOutput(final VirtPipesService virtPipesService,
			final UUID id) throws IOException {
		this.virtPipesService = virtPipesService;
		this.id = id;
		pipe = Pipe.open();
		pipe.source().configureBlocking(false);
		virtPipesService.getServerSocket().getChannelsPool().addChannel(
				pipe.source(), new ChannelStrategy(SelectionKey.OP_READ) {

					@Override
					protected void accept(final SelectionKey key,
							final SelectableChannel channel) {}

					@Override
					protected void closed(final SelectableChannel channel) {}

					@Override
					protected void connect(final SelectionKey key,
							final SelectableChannel channel) {}

					@Override
					protected void read(final SelectionKey key,
							final SelectableChannel channel) {

						final ByteBuffer readBuffer = ByteBuffer.allocate(1024);

						final SourceChannel socketChannel = (SourceChannel) key
								.channel();

						// Attempt to read off the
						// channel
						int numRead;
						try {
							numRead = socketChannel.read(readBuffer);
						} catch (final IOException e) {
							// The remote forcibly
							// closed the
							// connection, cancel
							// the selection key and
							// close the
							// channel.
							key.cancel();
							try {
								socketChannel.close();
							} catch (final IOException e1) {
								Activator.LOGGER
										.throwing(
												getClass().getName(),
												"read(SelecttionKey, SelectableChannel)",
												e1);
							}
							return;
						}

						if (numRead == -1) {
							// Remote entity shut the
							// socket down
							// cleanly. Do the
							// same from our end and
							// cancel the
							// channel.
							try {
								key.channel().close();
							} catch (final IOException e) {
								Activator.LOGGER
										.throwing(
												getClass().getName(),
												"read(SelectionKey, SelectableChannel)",
												e);
							}
							key.cancel();
							return;
						}

						Activator.LOGGER.info("OOS " + numRead + " > VP " + id
								+ " " + readBuffer.position() + " "
								+ readBuffer.array().length);

						readBuffer.flip();

						Activator.LOGGER.info("OOS " + numRead + " > VP " + id
								+ " " + readBuffer.position() + " "
								+ readBuffer.array().length);

						SelectableVirtPipeOutput.this.read(socketChannel,
								readBuffer, numRead);
					}

					@Override
					protected void write(final SelectionKey key,
							final SelectableChannel channel) {}

				});
	}

	/**
	 * Use this for writing information through this VirtPipeOutput. You should
	 * use Java NIO to Java IO class.
	 * 
	 * @return sink of VirtPipeOutput
	 */
	public SinkChannel getSink() {
		return pipe.sink();
	}

	private void read(final SourceChannel channel, final ByteBuffer buffer,
			final int numRead) {
		virtPipesService.send(id, buffer);
	}

}
