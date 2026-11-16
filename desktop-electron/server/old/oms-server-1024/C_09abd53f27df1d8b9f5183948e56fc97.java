import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_09abd53f27df1d8b9f5183948e56fc97
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.getMapValueByKey("110kV;220kV;500kV;",mapRow.get("F_DYDJ"));

    }
}
