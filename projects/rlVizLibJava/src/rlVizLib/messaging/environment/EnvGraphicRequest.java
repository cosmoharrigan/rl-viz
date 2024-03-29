/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package rlVizLib.messaging.environment;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.rlcommunity.rlglue.codec.RLGlue;
import rlVizLib.messaging.AbstractMessage;
import rlVizLib.messaging.GenericMessage;
import rlVizLib.messaging.MessageUser;
import rlVizLib.messaging.MessageValueType;
import rlVizLib.messaging.NotAnRLVizMessageException;
import org.rlcommunity.rlglue.codec.EnvironmentInterface;
import rlVizLib.messaging.AbstractResponse;
import rlVizLib.messaging.BinaryPayload;
import rlVizLib.messaging.interfaces.HasImageInterface;

public class EnvGraphicRequest extends EnvironmentMessages {

    public EnvGraphicRequest(GenericMessage theMessageObject) {
        super(theMessageObject);
    }

    public static Response Execute() {
        String theRequest = AbstractMessage.makeMessage(
                MessageUser.kEnv.id(),
                MessageUser.kBenchmark.id(),
                EnvMessageType.kEnvGetGraphic.id(),
                MessageValueType.kNone.id(),
                "NULL");

        String responseMessage = RLGlue.RL_env_message(theRequest);

        Response theResponse;
        try {
            theResponse = new Response(responseMessage);
        } catch (NotAnRLVizMessageException ex) {
            URL defaultURL=EnvGraphicRequest.class.getResource("/images/defaultsplash.png");
            theResponse = new Response(getImageFromURL(defaultURL));
        }
        return theResponse;
    }

    private static BufferedImage getImageFromURL(URL imageURL) {
        BufferedImage theImage = null;
        try {
            theImage = ImageIO.read(imageURL);
        } catch (IOException ex) {
            Logger.getLogger(EnvGraphicRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return theImage;
    }

    @Override
    public String handleAutomatically(EnvironmentInterface theEnvironment) {
        HasImageInterface castedEnv = (HasImageInterface) theEnvironment;

        BufferedImage theImage = getImageFromURL(castedEnv.getImageURL());
        Response theResponse = new Response(theImage);
        return theResponse.makeStringResponse();
    }

    @Override
    public boolean canHandleAutomatically(Object theEnvironment) {
        return (theEnvironment instanceof HasImageInterface);
    }

    public static class Response extends AbstractResponse {

        private BufferedImage theImage;

        public Response(BufferedImage theImage) {
            this.theImage = theImage;

        }

        public Response(String responseMessage) throws NotAnRLVizMessageException {
            try {
                GenericMessage theGenericResponse = new GenericMessage(responseMessage);
                String payLoad = theGenericResponse.getPayLoad();
                DataInputStream DIS = BinaryPayload.getInputStreamFromPayload(payLoad);
                theImage = ImageIO.read(DIS);
            } catch (IOException ex) {
                Logger.getLogger(EnvGraphicRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public String makeStringResponse() {
            try {
                BinaryPayload P = new BinaryPayload();
                DataOutputStream DOS = P.getOutputStream();
                ImageIO.write(theImage, "PNG", DOS);
                String theEncodedImage = P.getAsEncodedString();
                String theResponse = AbstractMessage.makeMessage(MessageUser.kBenchmark.id(), MessageUser.kEnv.id(), EnvMessageType.kEnvResponse.id(), MessageValueType.kStringList.id(), theEncodedImage);

                return theResponse;
            } catch (IOException ex) {
                Logger.getLogger(EnvGraphicRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        public BufferedImage getImage() {
            return theImage;

        }
    };
}
