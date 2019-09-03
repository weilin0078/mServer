package server.life;

public enum ElementalEffectiveness
{
    NORMAL, 
    IMMUNE, 
    STRONG, 
    WEAK, 
    NEUTRAL;
    
    public static ElementalEffectiveness getByNumber(final int num) {
        switch (num) {
            case 1: {
                return ElementalEffectiveness.IMMUNE;
            }
            case 2: {
                return ElementalEffectiveness.STRONG;
            }
            case 3: {
                return ElementalEffectiveness.WEAK;
            }
            case 4: {
                return ElementalEffectiveness.NEUTRAL;
            }
            default: {
                throw new IllegalArgumentException("Unkown effectiveness: " + num);
            }
        }
    }
}
