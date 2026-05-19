import { errorFromNativeEvent, nextHeightOnResize } from '../internal';

// ---------------------------------------------------------------------------
// nextHeightOnResize
// ---------------------------------------------------------------------------

describe('nextHeightOnResize', () => {
  // -- Happy path --

  it('returns the parsed height when it differs from the previous height', () => {
    expect(
      nextHeightOnResize({ rawHeight: '120', prevHeight: 0, hasError: false })
    ).toBe(120);
  });

  it('parses fractional heights as numbers', () => {
    expect(
      nextHeightOnResize({ rawHeight: '87.5', prevHeight: 0, hasError: false })
    ).toBe(87.5);
  });

  it('accepts zero as a valid new height when previous height was non-zero', () => {
    expect(
      nextHeightOnResize({ rawHeight: '0', prevHeight: 42, hasError: false })
    ).toBe(0);
  });

  // -- Dedup --

  it('returns null when the new height equals the previous height', () => {
    expect(
      nextHeightOnResize({ rawHeight: '80', prevHeight: 80, hasError: false })
    ).toBeNull();
  });

  it('returns null when both previous and new heights are 0 (subsequent dedup)', () => {
    expect(
      nextHeightOnResize({ rawHeight: '0', prevHeight: 0, hasError: false })
    ).toBeNull();
  });

  // -- isFirstResize --

  it('accepts height 0 on first resize so the view collapses for no-content', () => {
    expect(
      nextHeightOnResize({
        rawHeight: '0',
        prevHeight: 0,
        hasError: false,
        isFirstResize: true,
      })
    ).toBe(0);
  });

  it('still deduplicates 0→0 when isFirstResize is false', () => {
    expect(
      nextHeightOnResize({
        rawHeight: '0',
        prevHeight: 0,
        hasError: false,
        isFirstResize: false,
      })
    ).toBeNull();
  });

  it('isFirstResize defaults to false when omitted', () => {
    expect(
      nextHeightOnResize({ rawHeight: '0', prevHeight: 0, hasError: false })
    ).toBeNull();
  });

  // -- NaN guard --

  it('returns null when rawHeight is not a number', () => {
    expect(
      nextHeightOnResize({
        rawHeight: 'not-a-number',
        prevHeight: 0,
        hasError: false,
      })
    ).toBeNull();
  });

  it('returns null when rawHeight is an empty string', () => {
    expect(
      nextHeightOnResize({ rawHeight: '', prevHeight: 10, hasError: false })
    ).toBeNull();
  });

  it('returns null when rawHeight is whitespace only', () => {
    expect(
      nextHeightOnResize({ rawHeight: '   ', prevHeight: 10, hasError: false })
    ).toBeNull();
  });

  it('returns null when rawHeight is a non-numeric string', () => {
    expect(
      nextHeightOnResize({ rawHeight: 'abc', prevHeight: 10, hasError: false })
    ).toBeNull();
  });

  // -- Error gate --

  it('returns null when hasError is true, regardless of height', () => {
    expect(
      nextHeightOnResize({ rawHeight: '200', prevHeight: 0, hasError: true })
    ).toBeNull();
  });

  it('error gate takes precedence over a valid height change', () => {
    expect(
      nextHeightOnResize({ rawHeight: '150', prevHeight: 100, hasError: true })
    ).toBeNull();
  });

  it('error gate takes precedence over NaN input', () => {
    expect(
      nextHeightOnResize({
        rawHeight: 'garbage',
        prevHeight: 0,
        hasError: true,
      })
    ).toBeNull();
  });
});

// ---------------------------------------------------------------------------
// errorFromNativeEvent
// ---------------------------------------------------------------------------

describe('errorFromNativeEvent', () => {
  it('extracts message and name from the native event payload', () => {
    const result = errorFromNativeEvent({
      error: { message: 'boom', name: 'RenderFailed' },
    });
    expect(result).toEqual({ message: 'boom', name: 'RenderFailed' });
  });

  it('returns a plain object (not a frozen native event reference)', () => {
    const input = { error: { message: 'm', name: 'n' } };
    const result = errorFromNativeEvent(input);
    expect(result).not.toBe(input.error);
  });

  it('preserves empty strings as-is', () => {
    expect(errorFromNativeEvent({ error: { message: '', name: '' } })).toEqual({
      message: '',
      name: '',
    });
  });
});
