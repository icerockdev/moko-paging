//
//  LoginUI.swift
//  iosApp
//
//  Created by mdubkov on 02.06.2022.
//

import SwiftUI
import MultiPlatformLibrary
import mokoMvvmFlowSwiftUI
import Combine

struct LoginScreen: View {
    @StateObject private var viewModel: ListViewModel
    @State private var state: ResourceStateKs<NSArray, NSString> = .loading
    
    init(viewModel: ListViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
        viewModel.start()
    }
    
    var body: some View {
        LoginScreenState(
            state: $state,
            loadNextPage: { viewModel.onLoadNextPage() },
            refresh: { return try await viewModel.onRefreshSuspend() },
            onRetryPressed: { viewModel.onRetryPressed() }
        ).onReceive(viewModel.stateKs) { state = $0 }
    }
}

extension ListViewModel {
    var stateKs: AnyPublisher<ResourceStateKs<NSArray, NSString>, Never> {
        createPublisher(state).map {
            ResourceStateKs($0)
        }.eraseToAnyPublisher()
    }
}

private extension ResourceStateData where T == NSArray {
    var dataKs: [ListUnitKs] {
        self.data?.map { ListUnitKs($0 as! ListUnit) } ?? []
    }
}

struct LoginScreenState: View {
    @Binding var state: ResourceStateKs<NSArray, NSString>
    let loadNextPage: () -> Void
    let refresh: () async throws -> KotlinUnit
    let onRetryPressed: () -> Void
    
    var body: some View {
        switch state {
        case .empty:
            Text("Empty")
        case .loading:
            ProgressView()
        case .data(let data):
            ListScreenBody(
                listItems: data.dataKs,
                loadNextPage: loadNextPage,
                refresh: refresh
            )
        case .error(let error):
            Text("Error: \(error.error ?? "unknown")")
            Button {
                onRetryPressed()
            } label: {
                Text("Retry")
            }

        }
    }
}

struct LoginScreenStatePreview: PreviewProvider {
    @State static var emptyState: ResourceStateKs<NSArray, NSString> = .empty
    @State static var loadingState: ResourceStateKs<NSArray, NSString> = .loading
    @State static var errorState: ResourceStateKs<NSArray, NSString> = .error(ResourceStateError<NSString>(error: "Some Error"))
    
    static var previewLambda: () async throws -> KotlinUnit = {
        return KotlinUnit()
    }
    
    static var previews: some View {
        LoginScreenState(
            state: $emptyState,
            loadNextPage: { },
            refresh: previewLambda,
            onRetryPressed: { }
        )
        LoginScreenState(
            state: $loadingState,
            loadNextPage: { },
            refresh: previewLambda,
            onRetryPressed: { }
        )
        LoginScreenState(
            state: $loadingState,
            loadNextPage: { },
            refresh: previewLambda,
            onRetryPressed: { }
        )
    }
}
