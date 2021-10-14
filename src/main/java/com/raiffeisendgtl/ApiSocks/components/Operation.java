package com.raiffeisendgtl.ApiSocks.components;

public enum Operation {

    moreThan,
    lessThan,
    equal;

    public static Operation convertFromString(String operationString) {
        Operation operation;
        try {
            operation = Operation.valueOf(operationString);
        }
        catch (Throwable e) {
            throw new SocksException(SocksErrorCode.INCORRECT_PARAMS);
        }
        return operation;
    }

}