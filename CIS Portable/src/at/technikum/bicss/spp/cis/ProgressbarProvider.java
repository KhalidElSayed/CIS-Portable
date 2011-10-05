package at.technikum.bicss.spp.cis;

public interface ProgressbarProvider {
	public void startBackgroundProgress(String progessMessage);
	public void progressFinished();
}
