import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.DelayTrigger;
import net.beadsproject.beads.ugens.RecordToFile;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class Texture1 {

    public static void main(String[] args) throws IOException {
        //basic set up
        float clockInterval = 60000 / 135f;
        Clock c = new Clock(clockInterval);
        String name = MethodHandles.lookup().lookupClass().getSimpleName();
        RecordToFile recordToFile = new RecordToFile(2, new File("../output/" + name + ".wav"));
        AudioContext ac = AudioContext.getDefaultContext();
        ac.out.addDependent(recordToFile);
        recordToFile.addInput(ac.out);
        DelayTrigger timer = new DelayTrigger(64 * 16 * clockInterval, new Bead() {
            @Override
            protected void messageReceived(Bead message) {
                recordToFile.kill();
                ac.stop();
            }
        });
        ac.out.addDependent(timer);
        recordToFile.start();
        ac.start();
        //make music
        //kick drum

        //hi-hat

        //pattern 1

        //pattern 2

    }
}
