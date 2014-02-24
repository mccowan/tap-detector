package com.github.mccowan.common;

import com.google.common.collect.ImmutableList;
import junit.framework.TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TODO$(user): Class description
 *
 * @author com.github.mccowan
 */
public class ListsTest {
    @Test
    public void testBisect() throws Exception {
        final Lists.Bisection<Integer> bisect = Lists.bisect(ImmutableList.of(1, 10, 22, 38, 48, 10), 4);
        Assert.assertEquals(bisect.left(), ImmutableList.of(1, 10, 22, 38));
        Assert.assertEquals(bisect.right(), ImmutableList.of(10));

        final Lists.Bisection<Integer> bisect1 = Lists.bisect(ImmutableList.of(1), 0);
        Assert.assertTrue(bisect1.left().isEmpty());
        Assert.assertTrue(bisect1.right().isEmpty());
    }
}
