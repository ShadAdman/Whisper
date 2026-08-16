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
            url: "https://github.com/ShadAdman/Whisper/releases/download/2.40.60/Whisper.xcframework.zip",
            checksum: "787703d10ce9454d5d3adbbed7dc58958c00b58ddd8a6f6ca5cf8c579fd0ae6e")
    ]
)
