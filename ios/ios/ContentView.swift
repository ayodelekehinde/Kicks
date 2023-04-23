import SwiftUI
import shared
import UIKit
import AVFoundation


struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return AppKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {

    var body: some View {
        ComposeView()
            .preferredColorScheme(.dark)
                .ignoresSafeArea(.keyboard)
    }
}

struct Content_Preview: PreviewProvider {
    static var previews: some View{
        ContentView()
    }
}
