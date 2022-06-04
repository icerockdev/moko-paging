//
//  LoginScreenBody.swift
//  iosApp
//
//  Created by mdubkov on 02.06.2022.
//

import SwiftUI
import MultiPlatformLibrary

struct ListScreenBody: View {
    let listItems: Array<ListUnitKs>
    var loadNextPage: () -> Void
    var refresh: () async throws -> KotlinUnit
    
    var body: some View {
        List(listItems) { item in
            VStack {
                switch item {
                case .productUnit(let listUnitProductUnit):
                    Text("Product #\(listUnitProductUnit.id)")
                case .loading:
                    ProgressView()
                        .frame(height: 20)
                }
            }.onAppear {
                // if you scrolled through 80% of the list
                let index = listItems.firstIndex { $0.id == item.id }!
                if Double(index) >= 0.8 * Double(listItems.count) {
                    loadNextPage()
                }
            }
        }.listStyle(.grouped)
            .refreshable {
                let _ = try? await refresh()
            }
    }
}
             
extension ListUnitKs: Identifiable {
    public var id: Int {
        switch self {
        case .productUnit(let listUnitProductUnit):
            return Int(listUnitProductUnit.id)
        case .loading:
            return -1
        }
    }
}

struct ListScreenBody_Previews: PreviewProvider {
    static let email: Array<ListUnitKs> = [
        .productUnit(
            ListUnitProductUnit(
                id: Int64(1),
                title: "Milf"
            )
        ),
        .productUnit(
            ListUnitProductUnit(
                id: Int64(2),
                title: "Cookie"
            )
        ),
        .productUnit(
            ListUnitProductUnit(
                id: Int64(3),
                title: "Water"
            )
        ),
        .loading
    ]
    
    
    static var previews: some View {
        ListScreenBody(
            listItems: email,
            loadNextPage: { },
            refresh: { return KotlinUnit() }
        )
    }
}
