// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "Whisper",
    platforms: [
        .iOS(.v14),
        .macOS(.v11)
    ],
    products: [
        .library(
            name: "Whisper",
            targets: ["Whisper"])
    ],
    targets: [
        .binaryTarget(
            name: "Whisper",
            url: "https://github.com/ShadAdman/Whisper/releases/download/2.40.30/Whisper.xcframework.zip",
            checksum: "de0dbbab1e965c36f771aa57c547da72fe58f7840bc37fa8c208f5c35342a70e")
    ]
)
