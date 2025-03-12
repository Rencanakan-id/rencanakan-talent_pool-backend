package rencanakan.id.talentpool.mapper;

import org.springframework.beans.BeanUtils;

public class DTOMapper {

    /**
     * Maps an object of type S (source) to an object of type T (target).
     *
     * @param source      The source object to map from.
     * @param targetClass The class of the target object to map to.
     * @param <S>          The type of the source object.
     * @param <T>          The type of the target object.
     * @return A new instance of the target object with fields copied from the source.
     */
    public static <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Error during mapping", e);
        }
    }

    /**
     * Maps an object of type S (source) to an existing target object of type T.
     *
     * @param source The source object to map from.
     * @param target The existing target object to map to.
     * @param <S>    The type of the source object.
     * @param <T>    The type of the target object.
     */
    public static <S, T> void mapToExisting(S source, T target) {
        if (source == null || target == null) {
            return;
        }

        BeanUtils.copyProperties(source, target);
    }
}