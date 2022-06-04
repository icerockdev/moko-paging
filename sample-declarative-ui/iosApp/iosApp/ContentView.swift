import SwiftUI
import MultiPlatformLibrary
import Combine


struct ContentView: View {
    var body: some View {
        LoginScreen(
            viewModel: ListViewModel(
                withInitialValue: false,
                withError: false
            )
        )
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
