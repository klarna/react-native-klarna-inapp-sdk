//
//  MockKlarnaNetworkCoreInstance.swift
//  react-native-klarna-network-core-Unit-Tests
//
//  Created by Jorge Palacio on 2026-04-15.
//

import Foundation
import KlarnaNetworkCore
@testable import react_native_klarna_network_core

final class MockKlarnaNetworkCoreInstance: KlarnaNetworkCoreInstance {
    var integrationMetadata: KlarnaIntegrationMetadata?
    var stubbedSessionToken: String = ""
    var stubbedSessionTokenError: KlarnaNetworkCoreImplError?
    var stubbedClearSessionError: KlarnaNetworkCoreImplError?

    func sessionToken(onSuccess: @escaping (String) -> Void, onFailure: @escaping (KlarnaNetworkCoreImplError) -> Void) {
        if let error = stubbedSessionTokenError {
            onFailure(error)
        } else {
            onSuccess(stubbedSessionToken)
        }
    }

    func clearSession(onSuccess: @escaping () -> Void, onFailure: @escaping (KlarnaNetworkCoreImplError) -> Void) {
        if let error = stubbedClearSessionError {
            onFailure(error)
        } else {
            onSuccess()
        }
    }
}
