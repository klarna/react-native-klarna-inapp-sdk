export interface KlarnaProductEvent {
  readonly action: string;
  readonly params: { [key: string]: any };
}
