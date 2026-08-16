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
            url: "https://github.com/ShadAdman/Whisper/releases/download/1.200.70/Whisper.xcframework.zip",
            checksum: "e0033a762837f5f6113c0acff01309b3abafbc224bfaf456eba488c4da191ac0")
    ]
)
