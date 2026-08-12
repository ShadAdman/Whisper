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
            url: "https://github.com/ShadAdman/Whisper/releases/download/0.90.0/Whisper.xcframework.zip",
            checksum: "0000000000000000000000000000000000000000000000000000000000000000")
    ]
)
