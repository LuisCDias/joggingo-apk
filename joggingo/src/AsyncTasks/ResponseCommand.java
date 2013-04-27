package AsyncTasks;

public interface ResponseCommand {

	enum ERROR_TYPE {
		AUTHENTICATION, NETWORK, JSON, GENERAL
	};

	public void onError(ERROR_TYPE error);

	public void onResultReceived(Object... results);

}
