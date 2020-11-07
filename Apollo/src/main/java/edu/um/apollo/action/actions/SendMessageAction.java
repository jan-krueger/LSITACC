package edu.um.apollo.action.actions;

import edu.um.apollo.Apollo;
import edu.um.apollo.action.Action;
import edu.um.core.PersonRegister;
import edu.um.core.security.RSA;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.security.SymmetricEncryption;
import io.netty.channel.socket.SocketChannel;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
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
            socketChannel.writeAndFlush(
                    PacketFactory.createRequestPublicKeyPacket(getArg("receiver")).build()
            ).sync();
            return false;
        }
        return true;
    }

    @Override
    protected boolean execute(Apollo apollo, SocketChannel socketChannel) throws InterruptedException {
        List<PersonRegister.Entry> receivers = apollo.getPersonRegister().find(getArg("receiver"));
        for(PersonRegister.Entry receiver : receivers) {
            socketChannel.writeAndFlush(
                PacketFactory.createSendMessagePacket(
                        receiver.getPerson(),
                        getArg("message"),
                        receiver.getPerson().getPublicKey()
                ).build()
            );
        }
        return true;
    }

}
