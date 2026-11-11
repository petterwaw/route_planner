package pl.zdaze.common;

/**
 * Values of the {@code code} field in error responses. The full list and meaning is in
 * {@code docs/api.md} ("Błędy"); add new codes there and here in the same change.
 */
public enum ErrorCode {
    VALIDATION_ERROR,
    NOT_FOUND,
    INTERNAL_ERROR
}
