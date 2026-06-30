export const toSentenceCase = (str) => {
  if (!str || typeof str !== 'string') return str;
  return str.replace(/\b\w+/g, (word) => {
    if (word.length === 0) return word;
    return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
  });
};
