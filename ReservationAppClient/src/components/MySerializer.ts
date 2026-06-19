export const mySerializer = (body: Record<string, unknown>) => {
  const fd = new FormData();
  for (const [key, value] of Object.entries(body)) {
    if (value == null) continue;
    if (value instanceof Blob || value instanceof File) {
      fd.append(key, value);
    } else {
      fd.append(
        key,
        new Blob([JSON.stringify(value)], { type: "application/json" })
      );
    }
  }
  return fd;
};
