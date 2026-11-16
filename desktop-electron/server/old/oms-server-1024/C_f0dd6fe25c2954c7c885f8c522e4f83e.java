import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_f0dd6fe25c2954c7c885f8c522e4f83e
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapYesNo.get(mapRow.get("FLAG_HIDE1"));

    }
}
