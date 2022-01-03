import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.io.NonrealtimeIO;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.ugens.*;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class GenericBasic {

    final static float bpm = 135;
    final static int numBars = 16;

    public static void main(String[] args) throws IOException {
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        //basic set up
        AudioContext ac = new AudioContext(new NonrealtimeIO(), 512);
        AudioContext.setDefaultContext(ac);
        float clockInterval = 60000 / bpm;
        Clock c = new Clock(clockInterval);
        ac.out.addDependent(c);
        String name = MethodHandles.lookup().lookupClass().getSimpleName();
        RecordToFile recordToFileDrums = new RecordToFile(2, new File("output/" + name + " (with kick) " + (int)bpm + "bpm.wav"));
        RecordToFile recordToFileNoDrums = new RecordToFile(2, new File("output/" + name + " " + (int)bpm + "bpm.wav"));
        ac.out.addDependent(recordToFileDrums);
        ac.out.addDependent(recordToFileNoDrums);
        recordToFileDrums.addInput(ac.out);
        DelayTrigger timer = new DelayTrigger(numBars * 4 * clockInterval, new Bead() {
            @Override
            protected void messageReceived(Bead message) {
                recordToFileDrums.kill();
                recordToFileNoDrums.kill();
                ac.stop();
            }
        });
        ac.out.addDependent(timer);
        recordToFileDrums.start();
        recordToFileNoDrums.start();
        //make music
        //kick drum
        SamplePlayer spBd = new SamplePlayer(SampleManager.sample("Device/HappyBrackets/data/audio/Mattel_Drum_Machine/MatBd.wav"));
        spBd.setKillOnEnd(false);
        Gain gBd = new Gain(1, 0.5f);
        gBd.addInput(spBd);
        ac.out.addInput(gBd);
        c.addMessageListener(new Bead() {
            @Override
            protected void messageReceived(Bead message) {
                if(c.isBeat()) {
                    spBd.setPosition(0);
                }
            }
        });
        //synth outputs
        Envelope s1GainEnv = new Envelope(0f);
        Envelope s2GainEnv = new Envelope(0f);
        Gain s1Gain = new Gain(2, s1GainEnv);
        Gain s2Gain = new Gain(2, s2GainEnv);
        recordToFileNoDrums.addInput(s1Gain);
        recordToFileNoDrums.addInput(s2Gain);
        ac.out.addInput(s1Gain);
        ac.out.addInput(s2Gain);

        //end of generic code
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////

        //begin the texture code

        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        //synth 1
        Envelope s1Freq = new Envelope();
        Envelope s1FMDepth = new Envelope();
        Envelope s1FMRatio = new Envelope();
        Envelope s1LFORate = new Envelope();
        Envelope s1LFODepth = new Envelope();
        WavePlayer s1LFO = new WavePlayer(s1LFORate, Buffer.SINE);
        WavePlayer s1Modulator = new WavePlayer(0, Buffer.SINE);
        WavePlayer s1Carrier = new WavePlayer(0, Buffer.SINE);
        Function s1ModulationFunction = new Function(s1Freq, s1FMDepth, s1Modulator) {
            @Override
            public float calculate() {
                return x[0] + x[0] * x[1] * x[2];
            }
        };
        Function s1FMRatioFunction = new Function(s1Freq, s1FMRatio) {
            @Override
            public float calculate() {
                return x[0] * x[1];
            }
        };
        s1Carrier.setFrequency(s1ModulationFunction);
        s1Modulator.setFrequency(s1FMRatioFunction);
        Function s1LFOFunction = new Function(s1LFO, s1LFODepth, s1GainEnv) {
            @Override
            public float calculate() {
                return (x[0] * x[1] + 1) * x[2];
            }
        };
        s1Gain.setGain(s1LFOFunction);
        s1Gain.addInput(s1Carrier);

        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        //synth 2
        Envelope s2Freq = new Envelope();
        Envelope s2FMDepth = new Envelope();
        Envelope s2FMRatio = new Envelope();
        Envelope s2LFORate = new Envelope();
        Envelope s2LFODepth = new Envelope();
        WavePlayer s2LFO = new WavePlayer(s2LFORate, Buffer.SINE);
        WavePlayer s2Modulator = new WavePlayer(0, Buffer.SINE);
        WavePlayer s2Carrier = new WavePlayer(0, Buffer.SINE);
        Function s2ModulationFunction = new Function(s2Freq, s2FMDepth, s2Modulator) {
            @Override
            public float calculate() {
                return x[0] + x[0] * x[1] * x[2];
            }
        };
        Function s2FMRatioFunction = new Function(s2Freq, s2FMRatio) {
            @Override
            public float calculate() {
                return x[0] * x[1];
            }
        };
        s2Carrier.setFrequency(s2ModulationFunction);
        s2Modulator.setFrequency(s2FMRatioFunction);
        Function s2LFOFunction = new Function(s2LFO, s2LFODepth, s2GainEnv) {
            @Override
            public float calculate() {
                return (x[0] * x[1] + 1) * x[2];
            }
        };
        s2Gain.setGain(s2LFOFunction);
        s2Gain.addInput(s2Carrier);
        
        

        //patterns
        c.addMessageListener(new Bead() {
            @Override
            protected void messageReceived(Bead message) {
                //TODO ACTION HERE
                if(c.getCount() % 12 == 0) {
                    s1Freq.clear().setValue(Pitch.mtof(60));
                    s1GainEnv.clear().setValue(0.2f);
                    s1GainEnv.addSegment(0, 100);
                    s1FMRatio.clear().setValue(3.2f);
                    s1FMDepth.clear().setValue(0.5f);
                    s1LFORate.clear().setValue(0.1f);
                    s1LFODepth.clear().setValue(0.2f);
                }
                if(c.getCount() % 16 == 0) {
                    s2Freq.clear().setValue(Pitch.mtof(60));
                    s2GainEnv.clear().setValue(0.2f);
                    s2GainEnv.addSegment(0, 100);
                    s2FMRatio.clear().setValue(3.2f);
                    s2FMDepth.clear().setValue(0.5f);
                    s2LFORate.clear().setValue(0.1f);
                    s2LFODepth.clear().setValue(0.2f);
                }
            }
        });

        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        //must start ac last as this runs instantly in non-realtime mode
        
        ac.start();
    }
}
