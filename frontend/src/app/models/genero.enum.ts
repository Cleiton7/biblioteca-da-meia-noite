/**
 * Gêneros literários disponíveis para cadastro de livros.
 * Deve estar sempre sincronizado com o enum Genero do backend
 * (com.bibliotecameianoite.biblioteca.model.enums.Genero).
 */
export enum Genero {
  ROMANCE = 'ROMANCE',
  FICCAO_CIENTIFICA = 'FICCAO_CIENTIFICA',
  FANTASIA = 'FANTASIA',
  TERROR = 'TERROR',
  BIOGRAFIA = 'BIOGRAFIA',
  HISTORIA = 'HISTORIA',
  POESIA = 'POESIA',
  INFANTIL = 'INFANTIL',
  SUSPENSE = 'SUSPENSE',
  AVENTURA = 'AVENTURA',
  OUTRO = 'OUTRO'
}

/**
 * Rótulos amigáveis para exibição dos gêneros na interface.
 */
export const GENERO_LABELS: Record<Genero, string> = {
  [Genero.ROMANCE]: 'Romance',
  [Genero.FICCAO_CIENTIFICA]: 'Ficção Científica',
  [Genero.FANTASIA]: 'Fantasia',
  [Genero.TERROR]: 'Terror',
  [Genero.BIOGRAFIA]: 'Biografia',
  [Genero.HISTORIA]: 'História',
  [Genero.POESIA]: 'Poesia',
  [Genero.INFANTIL]: 'Infantil',
  [Genero.SUSPENSE]: 'Suspense',
  [Genero.AVENTURA]: 'Aventura',
  [Genero.OUTRO]: 'Outro'
};
