package joey.present.view.ui;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

public class ZhiBoChatMeter {
	static final private double EMA_FILTER = 0.6;

	private MediaRecorder chatRecorder = null;
	private double chatEMA = 0.0;

	public void start(String name) {
		if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}
		if (chatRecorder == null) {
			chatRecorder = new MediaRecorder();
			chatRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			chatRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			chatRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			chatRecorder.setOutputFile(android.os.Environment.getExternalStorageDirectory() + "/" + name);
			try {
				chatRecorder.prepare();
				chatRecorder.start();

				chatEMA = 0.0;
			} catch (IllegalStateException e) {
				System.out.print(e.getMessage());
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}

	public double getAmplitude() {
		if (chatRecorder != null) {
			return (chatRecorder.getMaxAmplitude() / 2700.0);
		} else {
			return 0;
		}
	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		chatEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * chatEMA;
		return chatEMA;
	}
}
