package me.songfei.snmputils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * A simple SNMP Client
 * @author southwolf
 */
public class SNMPNodeService {
    private String address;
    private String community;

    private Snmp snmp;
    private static Logger logger = LoggerFactory.getLogger(SNMPNodeService.class);

    public SNMPNodeService(String address, String community) {
        super();
        this.address = address;
        this.community = community;
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws IOException {
        snmp.close();
    }

    public void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    public String getAsString(OID oid) throws IOException {
        try {
            ResponseEvent event = get(new OID[]{oid});
            if(event == null || event.getResponse() == null || event.getResponse().size() == 0) {
                throw new IOException("SNMP Time out.");
            }

            return event.getResponse().get(0).getVariable().toString();
        }catch (NullPointerException e) {
            logger.debug("GET SNMP info error: ", e);
            throw new IOException("SNMP Time out.");
        }
    }

    public void getAsString(OID oids,ResponseListener listener) {
        try {
            snmp.send(getPDU(new OID[]{oids}), getTarget(),null, listener);
        } catch (IOException e) {
            logger.debug("GET SNMP info error: ", e);
            throw new RuntimeException(e);
        }
    }

    public ResponseEvent get(OID oids[]) throws IOException {
        ResponseEvent event = snmp.send(getPDU(oids), getTarget(), null);
        if(event != null) {
            return event;
        }
        logger.debug("GET timed out");
        throw new RuntimeException("GET timed out");
    }

    private PDU getPDU(OID oids[]) {
        PDU pdu = new PDU();
        for (OID oid : oids) {
            pdu.add(new VariableBinding(oid));
        }

        pdu.setType(PDU.GET);
        return pdu;
    }

    private Target getTarget() {
        Address targetAddress = GenericAddress.parse("udp:" + address + "/161");
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    public HashMap<String, String> walk(OID startOid) throws IOException {
        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        HashMap<String, String> walkResult = new HashMap<String, String>();
        List<TreeEvent> events = treeUtils.getSubtree(getTarget(), startOid);

        if(events == null || events.size() == 0){
            logger.debug("No result returned.");
            return null;
        }

        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                logger.debug(event.getErrorMessage());
                continue;
            }
            VariableBinding[] varBindings = event.getVariableBindings();
            if (varBindings == null || varBindings.length == 0) {
                continue;
            }
            for (VariableBinding varBinding : varBindings) {
                if (varBinding == null) {
                    continue;
                }
                walkResult.put(varBinding.getOid().toString(), varBinding.getVariable().toString());
            }
        }
        return walkResult;
    }
}
