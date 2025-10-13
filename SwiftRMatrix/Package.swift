// swift-tools-version: 6.2
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "SwiftRMatrix",
    dependencies: [
        .package(url: "https://github.com/apple/swift-argument-parser", from: "1.0.0"),
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .systemLibrary(
            name: "CRashunal",
            pkgConfig: "rashunal",
            providers: [
                .apt(["rashunal"]),
                .brew(["rashunal"]),
            ],
        ),
        .systemLibrary(
            name: "CRMatrix",
            pkgConfig: "rmatrix",
            providers: [
                .apt(["rmatrix"]),
                .brew(["rmatrix"]),
            ],
        ),
        .target(
            name: "Model",
            dependencies: [
                "CRashunal",
                "CRMatrix",
            ],
            path: "Sources/Model",
        ),
        .executableTarget(
            name: "SwiftRMatrix",
            dependencies: [
                .product(name: "ArgumentParser", package: "swift-argument-parser"),
                "Model",
            ],
            path: "Sources/SwiftRMatrix"
        ),
    ]
)
