package edu.um.apollo;

import edu.um.core.security.RSA;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.Packet;
import edu.um.core.protocol.packets.SendPersonPacket;
import edu.um.core.security.SymmetricEncryption;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ClientFilter extends SimpleChannelInboundHandler<Packet> {

    private final Apollo apollo;

    public ClientFilter(Apollo apollo) {
        this.apollo = apollo;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        switch (Packets.byId(packet.getId()).get()) {
            case GREET_CLIENT:
                apollo.setServerPublicKey(RSA.getPublicKey(packet.get("publicKey")));
                Apollo.LOGGER.info("Server is greeting you");
                break;

            case SEND_MESSAGE:
                SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(RSA.decrypt(packet.get("messageKey"), apollo.getPerson().getPrivateKey())), "AES");
                IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(RSA.decrypt(packet.get("ivParameterSpec"), apollo.getPerson().getPrivateKey())));

                System.out.println("RECEIVED message: " + SymmetricEncryption.decrypt(secretKeySpec, ivParameterSpec, packet.get("message")));
                break;


            case SEND_PERSON:
                SendPersonPacket sendPersonPacket = packet.as(SendPersonPacket.class);
                apollo.getPersonRegister().add(sendPersonPacket.getPerson());
                break;

            case ACK:
                apollo.getActionQueue().getCurrent().setSuccess(true);
                apollo.advanceActionQueue();
                break;

            case NAK:
                apollo.getActionQueue().getCurrent().setSuccess(false);
                apollo.advanceActionQueue();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
