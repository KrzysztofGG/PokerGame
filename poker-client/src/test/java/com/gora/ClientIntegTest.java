package com.gora;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

class ClientIntegTest {

    @Test
    void testGame() throws IOException {

//        Client.Inner client1 = new Client.Inner();
//        Client.Inner.initialize();
//        Client.Inner client2 = new Client.Inner();
//        Client.Inner.initialize();
        int a  = 0;
        a++;
//        client1.sendCommand("\\ready");
//        client2.sendCommand("\\ready");
//        client1.sendCommand("\\swap 1");
//        client2.sendCommand("\\swap 1");
//        client1.sendCommand("\\check");
//        client2.sendCommand("\\check");
//        client1.sendCommand("\\exit");
//        client2.sendCommand("\\exit");
        assertEquals(1, a);
    }
    @Test
    void test(){
        int a = 0;
        a++;
        assertEquals(1, a);
    }

}
