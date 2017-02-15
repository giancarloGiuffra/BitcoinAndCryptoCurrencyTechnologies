import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlockChainTest {

    @Test
    public void ShouldCreateABlockChainWithOnlyAGenesisBlock() throws Exception {

        KeyPair pair = KeyPair();
        Block genesisBlock = new Block(null, pair.getPublic());
        BlockChain blockChain = new BlockChain(genesisBlock);

        Block maxHeightBlock = blockChain.getMaxHeightBlock();
        assertThat(maxHeightBlock.getHash(), equalTo(null));
        assertThat(maxHeightBlock.getCoinbase(), equalTo(new Transaction(25, pair.getPublic())));
    }

    private KeyPair KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024, SecureRandom.getInstance("SHA1PRNG"));
        return generator.generateKeyPair();
    }

}