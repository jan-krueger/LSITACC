package edu.um.apollo.action.actions;

import edu.um.apollo.Apollo;
import edu.um.apollo.action.Action;
import edu.um.core.PersonRegister;
import edu.um.core.RSA;
import edu.um.core.protocol.PacketFactory;
import io.netty.channel.socket.SocketChannel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SendMessageAction extends Action {


    public SendMessageAction() {
        //TODO deal with max trials
        super(5);
    }

    @Override
    public boolean pre(Apollo apollo, SocketChannel socketChannel) throws InterruptedException {
        List<PersonRegister.Entry> receivers = apollo.getPersonRegister().find(getArg("receiver"));
        if(receivers.isEmpty()) {
            socketChannel.writeAndFlush(PacketFactory.createRequestPublicKeyPacket(getArg("receiver")).build()).sync();
            return false;
        }
        return true;
    }

    @Override
    protected boolean execute(Apollo apollo, SocketChannel socketChannel) throws InterruptedException {
        List<PersonRegister.Entry> receivers = apollo.getPersonRegister().find(getArg("receiver"));
        for(PersonRegister.Entry receiver : receivers) {
            try {
                socketChannel.writeAndFlush(
                    PacketFactory.createSendMessagePacket(apollo.getPerson(), receiver.getPerson().getId(),
                            RSA.encrypt(getArg("message"), RSA.getPublicKey(receiver.getPerson().getPublicKey()))
                    ).build()
                ).sync(); //TODO encrypt
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
